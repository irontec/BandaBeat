//
//  PlaylistPlayerViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "PlaylistDetailViewController.h"
#import "TrackViewCell.h"
#import "AppDelegate.h"
#import "Track.h"
#import "Playlist.h"
#import "PlayerViewController.h"
#import "MBProgressHUD.h"

#define kInfoCellNormal 68

@interface PlaylistDetailViewController ()
@property(nonatomic) AppDelegate *appDelegate;
@property(nonatomic) BLAPI *api;
@property(nonatomic) MBProgressHUD *hud;
@property(nonatomic) Playlist *playlist;
-(NSArray*)loadPlaylistFromBD;
-(void)loadDataForTableView;
-(void)updateTrackData;
@end

@implementation PlaylistDetailViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     self.trackedViewName = @"Track";
    
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];
    [self.tableView setBackgroundColor:[UIColor clearColor]];
    
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    _context = [_appDelegate managedObjectContext];
    self.navigationItem.title = self.playlistTitle;
    _hud = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:self.hud];
    _api = [BLAPI sharedInstance];
    [self loadDataForTableView];

}

-(void)loadDataForTableView
{
    _tracks = [self loadPlaylistFromBD];
    
   if(_tracks.count == 0 || [self.playlist.dirty compare:[NSNumber numberWithBool:YES]] == NSOrderedSame)
        [self updateTrackData];
    
}

-(void)updateTrackData
{
    self.hud.labelText = @"Eguneratzen";
    [self.hud showUsingAnimation:YES];
    
    [_api getSongsFromPlaylist:self.idPlaylist withHandler:^(NSString *methodName, id methodResult) {
        
        NSError *error;
        
        NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Playlist" inManagedObjectContext:_context];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        
        NSPredicate *predicate = [NSPredicate predicateWithFormat:
                                  @"idPlaylist == %i", [self.idPlaylist intValue]];
        
        [request setPredicate:predicate];
        [request setEntity:entityDesc];

        Playlist *localPlaylist = [[self.context executeFetchRequest:request error:&error] lastObject];
        
        [localPlaylist setDirty:[NSNumber numberWithBool:NO]];
        
        for (Track *track in _tracks) {
            [_context deleteObject:track];
        }
        
        NSInteger order = 0;
        for (NSDictionary* trackItem in methodResult) {
            
            Track *track = [NSEntityDescription insertNewObjectForEntityForName:@"Track" inManagedObjectContext:_context];
            [track setDataWithdictionary:trackItem order:order];
            [localPlaylist addTracksObject:track];
            order++;
            
        }
             
        //Guardamos todo y actualizamos la tabla
        if (![self.context save:&error]) {
            NSLog(@"failed with error %@", error);
        }
        
        _tracks = [self loadPlaylistFromBD];
        
        [self.tableView reloadData];
        
        [_hud hideUsingAnimation:YES];
        
    }];
    
}


-(IBAction)setFavorite:(id)sender
{
    UIButton *button = (UIButton*) sender;
    
    NSError *error = nil;
    
    Track *track = [_tracks objectAtIndex:button.tag];

    if (button.isSelected) {
        button.selected = NO;
        [track setFavorite:[NSNumber numberWithBool:NO]];
            
    } else {
        button.selected = YES;
        [track setFavorite:[NSNumber numberWithBool:YES]];
    }
    
    //Guardamos todo y actualizamos la tabla
    if (![_context save:&error]) {
        NSLog(@"failed with error %@", error);
    }
}


#pragma mark - Track CoreData methods
-(NSArray*)loadPlaylistFromBD
{
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Playlist" inManagedObjectContext:_context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:
                              @"idPlaylist == %i", [self.idPlaylist intValue]];
       
    [request setPredicate:predicate];
    [request setEntity:entityDesc];
    
    self.playlist = [[_context executeFetchRequest:request error:nil] lastObject];
    
    NSArray *tracks = [self.playlist.tracks allObjects];
    
    NSSortDescriptor *nameSort = [[NSSortDescriptor alloc] initWithKey:@"order" ascending:YES];
    NSArray *sortedArray = [tracks sortedArrayUsingDescriptors:@[nameSort]];
    
    return sortedArray;
}


#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_tracks count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    TrackViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TrackCell"];
        
    Track *track = [_tracks objectAtIndex:indexPath.row];
        
    [cell setTrack:track];
    //[cell.favoriteButton setTag:[track.idTrack intValue]];
    [cell.favoriteButton setTag:indexPath.row];
    
    UIView *bgColorView = [[UIView alloc] init];
    [bgColorView setBackgroundColor:[UIColor clearColor]];
    [cell setSelectedBackgroundView:bgColorView];
    
    NSLog(@"ORDER: %@", [track order]);
    
    return cell;
    
}


#pragma mark - Table view delegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return kInfoCellNormal;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _appDelegate.songsArray = _tracks;
    
    
    _appDelegate.playNextSongTriggered = YES;
    _appDelegate.triggredIndex = indexPath.row;
    _appDelegate.playerState = NotInitialized;
    
    [_appDelegate playSongAtIndex:indexPath.row];

    
    [self.tabBarController setSelectedIndex:3];
}


@end
