//
//  MyPlaylistViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "MyPlaylistViewController.h"
#import "PlaylistDetailViewController.h"
#import "AppDelegate.h"
#import "Playlist.h"
#import "PlaylistViewCell.h"
#import "NSString+IMAddition.h"
#import "MBProgressHUD.h"
#import "InformationView.h"
#import "HTMLViewController.h"
#import "SDWebImageManager.h"

#define kInfoCellNormal 68

@interface MyPlaylistViewController ()
@property(nonatomic) AppDelegate *appDelegate;
@property(nonatomic) BLAPI *api;
@property(nonatomic) MBProgressHUD *hud;
@property(nonatomic) NSManagedObjectContext *context;
@property(nonatomic) InformationView *infoView;
-(NSArray*)loadPlaylistFromBD;
-(void)loadDataForTableView;
-(void)updatePlaylistData;
@end

@implementation MyPlaylistViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     self.trackedViewName = @"My playlist";
    
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background.png"]]];
    [self.tableView setBackgroundColor:[UIColor clearColor]];
    
    //Se configuran los iconos de activado/desactivado de todos los TabBarItem del TabBar. En realidad esto es una ñapa.
    for (int i = 0; i < 4; i++) {
        UIViewController *c = [[self.tabBarController viewControllers] objectAtIndex:i];
        if (i == 0) {
            [c.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"playlist_ico"] withFinishedUnselectedImage:[UIImage imageNamed:@"playlist_ico"]];
        } else if(i == 1) {
            [c.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"favorites_ico"] withFinishedUnselectedImage:[UIImage imageNamed:@"favorites_ico"]];
        } else if(i == 2) {
            [c.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"miperfil_ico"] withFinishedUnselectedImage:[UIImage imageNamed:@"miperfil_ico"]];
        } else {
            [c.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"player_ico"] withFinishedUnselectedImage:[UIImage imageNamed:@"player_ico"]];
        }
    }
    
	_api = [BLAPI sharedInstance];
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    _context = [_appDelegate managedObjectContext];
    _hud = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:self.hud];
    
    [self loadDataForTableView];
}

-(void)viewWillAppear:(BOOL)animated
{
    [_api checkPlaylistUpdateWithHandler:^(NSString *methodName, id methodResult) {
        NSString *result = methodResult;
        if (![result isEqualToString:_api.generalToken]) {
            self.hud.labelText = @"Eguneratzen";
            [self.hud showUsingAnimation:YES];
            [self updatePlaylistData];
            _api.generalToken = result;
            [_api saveLoginPreferences];
            
        }
    }];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
    
    if (indexPath.section == 1) {

        Playlist *playlist = [_playlistArray objectAtIndex:indexPath.row];
        [segue.destinationViewController setIdPlaylist:playlist.idPlaylist];
        [segue.destinationViewController setPlaylistTitle:playlist.name];

    }
}

-(IBAction)help:(id)sender
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    HTMLViewController *vc = [sb instantiateViewControllerWithIdentifier:@"HTMLViewController"];
    [vc setFileName:@"help"];
    [self presentModalViewController:vc animated:YES];

}

-(void)alertCancel
{
    [_infoView removeFromSuperview];
}

-(void)loadDataForTableView
{
    _playlistArray = [self loadPlaylistFromBD];
    
    if (_playlistArray.count == 0) 
        [self updatePlaylistData];
    
    //Descargamos todas las imágenes para que aparezcan en la pantalla de bloqueo
    
    SDWebImageManager *manager = [SDWebImageManager sharedManager];
    
    for (Playlist *playlist in _playlistArray) {
        NSArray *tracks = [playlist.tracks allObjects];
        for (Track *track in tracks) {
            
            [manager downloadWithURL:[NSURL URLWithString:track.imageBig]
                            delegate:self
                             options:0
                             success:nil
                             failure:nil];;
        }
    }
}

-(void)updatePlaylistData
{
    self.hud.labelText = @"Eguneratzen";
    [self.hud showUsingAnimation:YES];
    
    [_api getAllPlaylistWithHandler:^(NSString *methodName, id methodResult) {

        NSError *error;
        NSManagedObjectContext *context = [_appDelegate managedObjectContext];
        
        for (Playlist* playlist in _playlistArray) {
            [_context deleteObject:playlist];
        }
        
        for (NSDictionary* playlistItem in methodResult) {
            Playlist *playlist = [NSEntityDescription insertNewObjectForEntityForName:@"Playlist" inManagedObjectContext:context];
            [playlist setDataWithdictionary:playlistItem];
        }
        
        //Guardamos todo y actualizamos la tabla
        if (![context save:&error]) {
            NSLog(@"failed with error %@", error);
        }
        
        _playlistArray = [self loadPlaylistFromBD];
        
        if (_playlistArray.count == 0) {
            
            if (_infoView == nil) {
                _infoView = [[InformationView alloc] initWithFrame:[self.navigationController.view bounds] message:@"Banda Beat webgunean sartu abestiak gehitzeko" withController:self];
                [self.navigationController.view addSubview:_infoView];
                [_infoView showMessage];
            }
        } else {
            if (_infoView != nil)
                [_infoView removeFromSuperview];
        }
        
        [self.tableView reloadData];
        [self.hud hide:YES];
    }];

}


#pragma mark - Playlist CoreData methods
-(NSArray*)loadPlaylistFromBD
{

    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Playlist" inManagedObjectContext:_context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    
    NSSortDescriptor *nameSort = [[NSSortDescriptor alloc] initWithKey:@"name" ascending:YES selector:@selector(localizedStandardCompare:)];
    [request setSortDescriptors:@[nameSort]];
    [request setEntity:entityDesc];
    
    NSError *error;
    
    _playlistArray = [_context executeFetchRequest:request error:&error];
    
    return _playlistArray;

}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0) {
        return 1;
    } else {
        return [_playlistArray count];
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"PlaylistCell";
    PlaylistViewCell *cell;
    
    if (indexPath.section == 0) {
               
        NSString *BBCellIdentifier = @"BBPlaylistCell";
        cell = [tableView dequeueReusableCellWithIdentifier:BBCellIdentifier];
        
        UIView *bgColorView = [[UIView alloc] init];
        [bgColorView setBackgroundColor:[UIColor clearColor]];
        [cell setSelectedBackgroundView:bgColorView];

    } else {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
        
        Playlist *playlist = [_playlistArray objectAtIndex:indexPath.row];
        
        cell.name.text = [playlist name];
        cell.songs.text = [NSString stringWithFormat:@"%i abesti", [[playlist songCount] intValue]];
        
        UIView *bgColorView = [[UIView alloc] init];
        [bgColorView setBackgroundColor:[UIColor clearColor]];
        [cell setSelectedBackgroundView:bgColorView];
    }

    return cell;
}

// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
}

#pragma mark - Table view delegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return kInfoCellNormal;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        [self performSegueWithIdentifier:@"BBPlaylistSegue" sender:indexPath];
    } else {
        Playlist *playlist = [_playlistArray objectAtIndex:indexPath.row];
        
        if (playlist.songCount != [NSNumber numberWithInt:0]) {
            [self performSegueWithIdentifier:@"PlaylistDetailSegue" sender:indexPath];
        } else {
            UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Abisua"
                                                              message:@"Zerrenda ez dauka abestirik"
                                                             delegate:nil
                                                    cancelButtonTitle:@"Ok"
                                                    otherButtonTitles:nil];
            [message show];
        }
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


@end
