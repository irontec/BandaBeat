//
//  PlaylistPlayerViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "BBPlaylistDetailViewController.h"
#import "TrackViewCell.h"
#import "AppDelegate.h"
#import "Track.h"
#import "Playlist.h"
#import "PlayerViewController.h"
#import "MBProgressHUD.h"

#define kInfoCellNormal 68

@interface BBPlaylistDetailViewController ()
@property(nonatomic) AppDelegate *appDelegate;
@property(nonatomic) BLAPI *api;
@property(nonatomic) MBProgressHUD *hud;
-(void)loadDataForTableView;
-(void)updateTrackData;
@end

@implementation BBPlaylistDetailViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     self.trackedViewName = @"BB track";
    
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];
    [self.tableView setBackgroundColor:[UIColor clearColor]];
    
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    self.navigationItem.title = self.playlistTitle;
    _hud = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:self.hud];
    _api = [BLAPI sharedInstance];
    [self loadDataForTableView];
    
}

-(void)loadDataForTableView
{
    [self updateTrackData];
}

-(void)updateTrackData
{
    self.hud.labelText = @"Eguneratzen";
    [self.hud showUsingAnimation:YES];
    
    _tracks = [[NSMutableArray alloc] init];
    
    [_api getPublicSongs:self.idPlaylist withHandler:^(NSString *methodName, id methodResult) {
        
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Track" inManagedObjectContext:[_appDelegate managedObjectContext]];
     
        for (NSDictionary* trackItem in methodResult) {
            Track *track = [[Track alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
            [track setDataWithdictionary:trackItem order:1];
            [_tracks addObject:track];
            
        }
        
        [self.tableView reloadData];
        
        [_hud hideUsingAnimation:YES];
        
    }];
    
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
