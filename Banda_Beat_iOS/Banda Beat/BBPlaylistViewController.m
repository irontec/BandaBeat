//
//  MyPlaylistViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "BBPlaylistViewController.h"
#import "PlaylistDetailViewController.h"
#import "AppDelegate.h"
#import "Playlist.h"
#import "PlaylistViewCell.h"
#import "NSString+IMAddition.h"
#import "MBProgressHUD.h"
#import "InformationView.h"
#import "HTMLViewController.h"

#define kInfoCellNormal 68

@interface BBPlaylistViewController ()
@property(nonatomic) AppDelegate *appDelegate;
@property(nonatomic) BLAPI *api;
@property(nonatomic) MBProgressHUD *hud;
-(void)loadDataForTableView;
-(void)updatePlaylistData;
@end

@implementation BBPlaylistViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     self.trackedViewName = @"BB playlist";
    
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background.png"]]];
    [self.tableView setBackgroundColor:[UIColor clearColor]];
    
	_api = [BLAPI sharedInstance];
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    _hud = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:self.hud];
    
    [self loadDataForTableView];
}


-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
     
    Playlist *playlist = [_playlistArray objectAtIndex:indexPath.row];
    [segue.destinationViewController setIdPlaylist:playlist.idPlaylist];
    [segue.destinationViewController setPlaylistTitle:playlist.name];

}



-(void)loadDataForTableView
{
    [self updatePlaylistData];
    [self.tableView reloadData];
}

-(void)updatePlaylistData
{
    self.hud.labelText = @"Eguneratzen";
    [self.hud showUsingAnimation:YES];
    
    _playlistArray = [[NSMutableArray alloc] init];
    
    [_api getPublicPlaylistWithHandler:^(NSString *methodName, id methodResult) {
        
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Playlist" inManagedObjectContext:[_appDelegate managedObjectContext]];
       
        
        for (NSDictionary* playlistItem in methodResult) {
            Playlist *playlist = [[Playlist alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
            [playlist setDataWithdictionary:playlistItem];
            [_playlistArray addObject:playlist];
        }
        
        [self.hud hideUsingAnimation:YES];
        
        [self.tableView reloadData];
        
    }];
    
}


#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_playlistArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"PlaylistCell";
    PlaylistViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    Playlist *playlist = [_playlistArray objectAtIndex:indexPath.row];
        
    cell.name.text = [playlist name];
    cell.songs.text = [NSString stringWithFormat:@"%i abesti", [[playlist songCount] intValue]];
        
    UIView *bgColorView = [[UIView alloc] init];
    [bgColorView setBackgroundColor:[UIColor clearColor]];
    [cell setSelectedBackgroundView:bgColorView];
    
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
    [self performSegueWithIdentifier:@"BBPlaylistDetailSegue" sender:indexPath];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


@end
